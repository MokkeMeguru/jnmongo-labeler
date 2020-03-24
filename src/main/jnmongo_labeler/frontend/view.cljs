(ns jnmongo-labeler.frontend.view
  (:require
   [re-frame.core :as rf]
   [reagent.core :as r]
   [jnmongo-labeler.frontend.events :as events]
   [jnmongo-labeler.frontend.subs :as subs]
   [jnmongo-labeler.utils.parser :as parser]))

(defn navbar []
  [:nav#nav-box.navbar.box
   [:div.navbar-start>p "JNMongo Labeler"]
   [:div.navbar-end [:a {:href "https://mokkemeguru.github.io/portfolio/"} "contact"]]
   ])

(defn workspace-title []
  (let [title (r/atom "")]
    [:div.columns>div.column.is-full
     [:div>h2 "title > " (if @(rf/subscribe [::subs/contents]) "エクス・アルビオ")]
     [:div
      [:input {:type "text"
               :style {:font-size "1rem"}
               :on-key-down #(when (= 13 (.-keyCode js/window.event))
                               (rf/dispatch-sync [::events/load-static-file]))
               :on-change #(reset! title (-> % .-target .-value))
               :placeholder
               (if-not @(rf/subscribe [::subs/contents])
                 "<not selected>"
                 "エクス・アルビオ")}]]]))

(defn getSelected []
  (cond
    (js/window.getSelection)
    ["get" (js/window.getSelection)]
    (js/document.getSelection)
    ["get" (js/document.getSelection)]
    (js/document.selection)
    ["range" (.. js/document -selection createRange)]
    :else nil))

(defn view-section [section]
  (let [original section
        keywords (r/atom (set []))
        on-mouse-up-event (fn [] (when-let [[type selection] (getSelected)]
                                  (swap! keywords conj (str selection))))
        keywords-list (fn []
                        [:div.box {:style {:max-height "200px"
                                            :overflow-y "auto"
                                            :overflow-x "auto"}}
                         [:ul
                          (map (fn [k]
                                 [:li [:div.columns.is-vcentered
                                       [:div.column.is-9>p.has-text-justified k]
                                       [:div.column.is-3>button.button.is-info.is-light.is-small
                                        {:on-click #(swap! keywords disj k)} "X"]]])
                               @keywords)]])]
    [:div.column>div.columns
     [:div.column.is-9>div.card>div.card-content
       {:on-mouse-up on-mouse-up-event}
      (parser/parse section)]
     [:div.column
      [keywords-list]]]))

(defn workspace []
  [:div.columns>div.column.is-8.is-offset-2
   (workspace-title)
   [:div.columns
    [:div.column.is-four-fifths.has-text-centered>p "HTML viewer"]
    [:div.column>p "keywords viewer"]]
   (if-let [contents  @(rf/subscribe [::subs/contents])]
     (map #(view-section %) (:article contents)))])

(defn main []
  [:div
   (navbar)
   (workspace)])
