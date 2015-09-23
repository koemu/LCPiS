autoscale: true
slidenumbers: true
footer: Yuichiro Saito

# Learning Concurrent Programming in Scala

## 第3章 前半

### 久野研究室 2015年 輪読会

斎藤 祐一郎

---

## 留意事項

Code/Slide: [github.com/koemu/LCPiS](https://github.com/koemu/LCPiS)

```
$ brew info sbt
sbt: stable 0.13.9 (bottled)

$ scala -version
Scala code runner version 2.11.7 -- Copyright 2002-2013, LAMP/EPFL

$ java -version
java version "1.7.0_04"
Java(TM) SE Runtime Environment (build 1.7.0_04-b21)
Java HotSpot(TM) 64-Bit Server VM (build 23.0-b21, mixed mode)

$ sysctl -n machdep.cpu.brand_string  
Intel(R) Core(TM) i7-4578U CPU @ 3.00GHz
(2cores 4threads)
```

---

## 3章: Traditional Building Blocks of Concurrency

- ※冒頭の一節は、C++の大家であるビャーネ・ストロヴストルップ先生の言葉です。

- 2章でJVM上の並列プログラミングの基本について確認した。
- しかし、低レベルな部分ではエラーが出やすかったりデリケートだったりする。
    - データ競合、再整列、表示、デッドロック、非決定性…
- 幸運なことに、私たちには典型的な並列プログラミングに関するより応用的な積み木(ライブラリ)がある！
- その使い方について学ぶ。

---

- 一般的に、並列プログラミングには2つの側面がある。
    - 並列プログラミングで実装してみたい。
    - 並列でデータにアクセスしたい。
- 2章で学んだように、並列プログラムはスレッドごとに定義し開始することができる。本章では、より軽量に実装する方法を学ぶ。
- 2章で `synchronized` ステートメント、揮発性のある変数を用いるのは確認した。本章では、より詳細に迫る。

---

- (続き)
    - `Executor`, `ExecutionContext` オブジェクトの利用。
    - 原始性のある単純なノンブロッキング同期
    - 並列中の遅延変数とのやりとり
    - 並列キュー、セット、マップ。
    - プロセスの作成と通信のしかた。
- 究極の目標は、並列でも安全にファイルをハンドリングするプログラムをインプリすること。本章では再利用可能なファイルハンドリングAPIをつくる。

---

## The Executor and ExecutionContext objects

- 2章で、スレッド生成はプロセス生成よりも計算負荷の小さいこと、オブジェクトを確保するよりも大きいこと、モニターロックの獲得、エントリの中のコレクション更新について議論した。
    - アプリケーションは数多くの小さな並列タスクと高いスループットを求められる。
    - 全てのタスクにおいてフレッシュなスレッドを作ることはできない
- スレッドの起動には、メモリのアサイン、コンテキストスイッチが求められる。
- そこで **スレッドプール** : 多くの並列プログラミングフレームワークには、あらかじめスレッドを起動して、必要に応じてタスクを割り当てられるようになっている。

---

- プログラマは、並列タスクの実行内容をどのようにするかカプセル化できる。
    - JDKでは抽象的に `Executor.ExecutorRunnable` というシンプルなインタフェイスがある。1つの`execute`メソッドを割り当てられる。
    - `Runnable`オブジェクトを用意し、`run()`メソッドが呼ばれるようになっている。
- `Executor`オブジェクトは定義された`execute`か`Runnable`オブジェクトを呼び出し元のスレッドから開始する。これらはスレッドプールから呼び出される。
- JDK7には`ForkJoinPool`がある。ScalaではJDK6でも使える。
    - コード解説。 (p65)
    - SBTの`fork`設定を`false`にすると`sleep(500)`のコードは不要とのこと。
    - 私の環境だと設定無しに`sleep`なしでもちゃんと動きました。皆様検証ください。

---

- なんで`Executor`を最初に持ってこないと行けないか？先のサンプルでは簡単に`Executor`の実装を`Runnable`の変更無しに変えることが可能。なぜなら切り離されて動いているから。
- このあたり、焦点絞って動きをより探ってみる。
    - `ForkJoinPool`クラスが`ExecutorService`を呼び出す。こいつは便利なメソッドがいくつか用意されていて、`shutdown`が最重要。
    - `shutdown`メソッドは、Graceful shutdownができる(ミドルウェアには重要ですこれ)。
    - `awaitTermination`メソッドを定義すると、スレッド終了までの待機時間を定義可能。
    - コード解説。 (p65_2)

---

- `scala.concurrent`パッケージの`ExecutionContext`は`Executor`オブジェクトと同じ、またはそれ以上の機能がある。
    - オブジェクトのコンテキストの抽象`executor`メソッドは`Executor`のそれと同じ。更に`reportFailure`メソッドがあり、例外を投げられる。
    - デフォルトコンテキストである`global`コンテキストは、内部で`ForkJoinPool`インスタンスを持っている。
    - コード解説。 (p66)

---

- `ForkJoinPool`で生成したスレッドプールを`ExecutionContext`で使うこともできる。
    - コード解説。 (p67)
- `execute`をブロックコードでも書ける。
    - コード解説。 (p67_2)
- `Executor`や`ExecutionContext`は素晴らしい並列プログラミングの抽象部分を持っているが、問題が無い訳ではない。
    - スループットを確保するためにスレッドを再利用する機構が仇となることがある。
    - コード解説。 (p67_3) ※秒を変えて実行

---

※皆さんの環境でもお試しください。マシンスペックにより結果が変わるはずです。

```
$ sysctl -n machdep.cpu.brand_string  
Intel(R) Core(TM) i7-4578U CPU @ 3.00GHz

$ sbt run                                                                            
[info] Set current project to p67_3 (in build file:/Users/saito/repos/LCPiS/p67_3/)
[info] Running ExecutionContextSleep
ForkJoinPool-1-worker-1: Task 2 completed.
ForkJoinPool-1-worker-5: Task 0 completed.
ForkJoinPool-1-worker-7: Task 3 completed.
ForkJoinPool-1-worker-3: Task 1 completed.
ForkJoinPool-1-worker-1: Task 4 completed.
ForkJoinPool-1-worker-7: Task 6 completed.
ForkJoinPool-1-worker-3: Task 7 completed.
ForkJoinPool-1-worker-5: Task 5 completed.
ForkJoinPool-1-worker-3: Task 10 completed.
ForkJoinPool-1-worker-5: Task 11 completed.
ForkJoinPool-1-worker-1: Task 8 completed.
ForkJoinPool-1-worker-7: Task 9 completed.
ForkJoinPool-1-worker-3: Task 12 completed.
ForkJoinPool-1-worker-1: Task 14 completed.
ForkJoinPool-1-worker-7: Task 15 completed.
ForkJoinPool-1-worker-5: Task 13 completed.
ForkJoinPool-1-worker-5: Task 19 completed.
ForkJoinPool-1-worker-3: Task 16 completed.
[success] Total time: 11 s, completed 2015/09/24 0:16:45
```

---

- 全てのスレッドが2秒で終わるはずだが、そうはならない。
    - 4cores 8threadsのCPUだと、`ExecutionContext`はスレッド数分の8スレッドをスレッドプールに用意する。
    - あらかじめ用意されたスレッド数以上を実行すると、ブロックイディオムにガードされる(2章参照)。`notify`が呼ばれると改めて立ち上がる。
    - 最初の2秒で同時に8つ、次の2秒で次の8つ…とやっていくと、8秒強かかっておわる。
    - 10秒経つと、親スレッドが終了する。
    - (そう、32threadsのマシンなら2秒で終わる！7threads未満だと32回実行できません。)
- 解放されるまで永遠に実行がブロックされる。このような状況を **スタベーション** という。

---

## Atomic primitives

- 2章で、適切な同期が適用されない限り、(共有)メモリへの書き込みは直ちに反映されないことを学んだ。
    - 原始性
- 先行発生の関係が担保されることにより可視性が確認できた上で、`synchronized`ステートメントが信頼を得る。(訳？)
- Volatileフィールドはより軽量な先行発生の関係の担保方法だが、同期構造は弱い。
- `getUniqueId`メソッドを正しく実装するには？(訳？)
- 本章では、複数箇所からの読み書き可能なAtomic変数について学ぶ。
    - Atomic変数はVolatile変数の兄弟分みたいなものだが、より豊か(強力)。
    - より複雑な並列操作を`synchronized`ステートメントの信頼無しに実行可能。

---

### Atomic variables

- **Atomic変数** とは、Complex Linearizable Operationを可能になした記憶域である。
- **Linearizable Operation** とは、システム内においてあらゆる同時操作ができるものである。
    - 例: Volatileな書き込み。
- **Complex Linearizable Operation** とは、少なくとも同時2並列の読み書きが発生するLinearizable Operationと同義。
    - ここでの原始性は、Complex Linearizable Operationを指す。
- 各種のAtomic変数は`java.util.concurrent.atomic`パッケージに定義され、Complex Linearizable OperationはBoolean, integer, long, refrence型でサポートされている。
- 2章で出て来た`getUniqueId`を、`AtomicLong`を使って再実装してみる。
    - コード参照。 (p69)

---

- Atomic変数は、`getAndSet`メソッドとして、別の実装方法もある。数値型だと`decrementAndSet`もいる。
- `compareAndSet`は、基本的なAtomic変数の実装である。CASと呼ばれることもある。
     - 教科書 pp.70のコード参照。
     - ロックフリーである。
- CASで書き直したコードを見てみる。
    - コード参照。 (p70)
    - 教科書 pp.71のフロー図参照。
    - Tail recursion をしてスタック溢れを防止している点に注目。
