(ns jnmongo-labeler.frontend.view
  (:require
   [re-frame.core :as rf]
   [reagent.core :as r]
   [jnmongo-labeler.frontend.events :as events]
   [jnmongo-labeler.frontend.subs :as subs]
   [jnmongo-labeler.utils.parser :as parser]))

(defn navbar []
  [:nav#nav-box.navbar.box])

(defn workspace-title []
  (let [title (r/atom "")]
    [:div.columns>div.column.is-full
     [:div>h2 "title > " (if @(rf/subscribe [::subs/contents]) "エクス・アルビオ")]
     [:div
      [:input {:type "text"
               :on-key-down #(when (= 13 (.-keyCode js/window.event))
                               (rf/dispatch-sync [::events/load-static-file]))
               :on-change #(reset! title (-> % .-target .-value))
               :placeholder
               (if-not @(rf/subscribe [::subs/contents])
                 "<not selected>"
                 "エクス・アルビオ")}]]]))

(defn workspace []
  [:div.columns>div.column.is-8.is-offset-2
   (workspace-title)
   [:div.columns
    [:div.column.is-four-fifths>div.columns.is-3
     [:div.column
      [:p "HTML viewer"]
      (if-let [contents  @(rf/subscribe [::subs/contents])]
        [:div (map #(do [:div.card>div.card-content (parser/parse %)])  (:article contents))]
        [:p "hoge"])]
     ]
    [:div.column>div.columns [:div.column "keywords viewer"]]]
   ])

(defn main []
  [:div
   (navbar)
   (workspace)])
