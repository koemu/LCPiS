autoscale: true
slidenumbers: true
footer: Yuichiro Saito

# Learning Concurrent Programming in Scala

## 第5章 前半

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

## 5章: Data-Parallel Collections

- ※冒頭の一節は、TeXの生みの親でもあるドナルド・クヌース先生の言葉です。ついつい時期尚早な最適化ってやってしまいがちですよね〜

- 振り返り
    - ここまで、マルチスレッドを利用した並列プログラミングについて学習した。
    - そうすることで、正確さを保証できることに焦点を当ててきた。
    - 並行プログラミングからブロッキングをなくし、非同期な計算を、そして並列したデータ構造をスレッド間で通信するのかを理解した。
    - ツールを利用すると楽にできる。

---

- 本章では
    - 良いパフォーマンスをもたらす方法について焦点を当てる。
    - 既に存在するプログラムをあまり変えること無く、処理時間を短くする方法を学ぶ。
    - 前の章の内容でもできんことは無いけど、比較的重たい・非効率な時にやれる。
- **Data Palarellism** とは異なったデータエレメントを持つ同一の計算処理を続ける方法。
- 同期をもちいた並列計算処理よりも、並行データ処理、ゆくゆくはマージする。(？)
- 並行なデータを入力(多くはデータセット)し、また違ったデータセットを出力する。

---

- 本章は次のトピックを取り上げる
    - データ並行の操作
    - 並行度の設定
    - パフォーマンスの計測とその重要性
    - 直列処理と並行処理の違い
    - 並行コレクションと並列コレクション
    - カスタム並行コレクションの実装
    - その他 データ並行フレームワークについて

---

### Scala collections in a nutshell

- Scalaのコレクションモジュールは標準ライブラリにパッケージングされており、一般的な利用に向いたコレクションタイプにまとめられている。
- 関数を組み合わせることで、一般的かつ簡単に宣言的に操作ができる。
- P.138の真ん中の例では、`filter`コンビネータを使って0〜10万字の反対になった文字列を並べることができる。
- 3つの基本コレクションタイプ: sqeuences, maps, sets
- sequencesは、`apply`メソッドを用いると、インデックスを検索することができる。
- mapsは、Key-Value形式で、Keyに基づき値を検索できる。
- setsは`apply`メソッドを用いることで、エレメントのメンバーを検索できる。

---

- Immutable CollectionとMutable Collectionがある。前者は生成後は変更不可、後者は変更可能である。
    - 前者: Vector, ArrayBuffer
    - 後者: HashMap, HashSet
- `par`メソッド: データを並行に処理する
    - P.138 末尾の例を参照
- この後詳しく学んで行く。

---

### Using parallel collections

- 多くの並列プログラミングユーティリティでは、他のスレッドとのデータのやり取りができた。
- アトミック変数、同期ステートメント、コンカレントキューなど、並列プログラムが正確さを保証していた。
- 並行コレクションプログラミングモデルでは、直列のScalaコレクションとして一致した大きなものとしてデザインされている。
- 並行コレクションは、それ1つだけで実行時間を改善できる。
- 本節では、並行コレクションを使った時の実行時間差を見てみる。
- この後の共通ライブラリとなるコード解説 (P.139 中央)
    - JVMによって最適化がかかるため、その影響を排除するため、bodyブロックを呼び出すときにその影響を排除するよう計算する。

---

- プログラムのパフォーマンスの要因はいろいろあり、予測も大変。そこで仮説を評価して行く。
- コード解説 (p139_1)
    - Vectorクラスに5百万件のデータを放り込む
    - そんでもってシャッフル
    - 2つの方法で最大値を探す
    - 実行結果例は次のページに
---

```
$ sbt run
[info] Set current project to p139_1 (in build file:/Users/saito/repos/LCPiS/src/p139_1/)
[info] Running ParBasic
largest number 4999999
run-main-0: Sequential time 109.569 ms
largest number 4999999
run-main-0: Parallel time 681.545 ms
[success] Total time: 5 s, completed 2015/10/29 19:57:21
```

- 実習していて困ったことが発生
    - 教科書では並行の方が速いのだが、私の手元だと直列の方が速い。

---

```
$ brew info sbt
sbt: stable 0.13.9 (bottled)
$ scala -version
Scala code runner version 2.11.7 -- Copyright 2002-2013, LAMP/EPFL
$ java -version
java version "1.7.0_60"
Java(TM) SE Runtime Environment (build 1.7.0_60-b19)
Java HotSpot(TM) 64-Bit Server VM (build 24.60-b09, mixed mode)
$ sysctl -n machdep.cpu.brand_string
Intel(R) Core(TM) i5-4250U CPU @ 1.30GHz

$ sbt run
[info] Set current project to p139_1 (in build file:/Users/koemu/repos/LCPiS/src/p139_1/)
[info] Running ParBasic
largest number 4999999
run-main-0: Sequential time 128.808 ms
largest number 4999999
run-main-0: Parallel time 114.129 ms
[success] Total time: 5 s, completed 2015/10/29 20:29:08
```
