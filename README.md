# Widget Collection (ウィジェットコレクション)

多彩な機能とデザインを備えたAndroid向けホーム画面ウィジェットの詰め合わせアプリです。時計、カレンダー、スケジュール、メモ帳など、日常で役立つウィジェットを自分好みにカスタマイズして配置できます。

## 🌟 機能 (Features)

* **アナログ時計 & デジタル時計 (Analog & Digital Clock)**
    * 豊富なカスタムフォントとカラーで、自分好みの時計ウィジェットを作成可能です。
* **カレンダー & スケジュール (Calendar & Schedule)**
    * 予定の確認や管理をホーム画面から直感的に行えるウィジェットです。
* **シンプルメモ & 今日のメモ (Simple Memo & Today's Memo)**
    * サッと書き込めるメモ帳機能。TODOリストやちょっとした備忘録に最適です。
* **圧倒的なカスタマイズ性 (Highly Customizable)**
    * 数十種類に及ぶカスタムフォント、文字色、フレームデザインをウィジェット配置時の設定画面（Configure Activity）から細かく調整できます。

## 🛠️ 使用技術 (Tech Stack)

* **言語:** Java
* **プラットフォーム:** Android
* **UI・デザイン:** XML / RemoteViews (AppWidgetProvider)

## 📁 主要なディレクトリ構成 (Directory Structure)

* `src/main/java/...`
    * `/AnalogClock`, `/DigitalClock`: 時計ウィジェットのロジック
    * `/calendar`, `/Schedule`: カレンダーと予定管理
    * `/simple_memo`, `/todays_memo`: メモ帳機能
    * `/configure`: ウィジェット配置時の色やフォントの設定画面
* `src/main/assets/fonts/`: ウィジェットのデザインを彩る大量のカスタムフォントデータ
* `src/main/res/xml/`: 各種ウィジェットの定義ファイル（`appwidget-provider`）

## 🚀 使い方 (Getting Started)

1. リポジトリをローカルにクローンします。
   ```bash
   git clone [https://github.com/rayray-swamp/Widget-Collection.git](https://github.com/rayray-swamp/Widget-Collection.git)
