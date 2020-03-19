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
                                  (swap! keywords conj (str selection))
                                   ;; (condp = type
                                   ;;   "get"
                                   ;;   (let [range (.getRangeAt selection 0)
                                   ;;         new-node
                                   ;;         (doto
                                   ;;             (js/document.createElement "span")
                                   ;;           (#(set! (.-className %) "selectedText"))
                                   ;;           (#(set! (.-innerHTML %) (str selection))))]
                                   ;;     (doto range
                                   ;;       .deleteContents
                                   ;;       (.insertNode new-node)))
                                   ;;   "range"
                                   ;;   (println "var"))
                                   ))
        keywords-list (fn []
                        [:ul.box {:style {:max-width "200px"}}
                         (map (fn [k]
                                [:li [:div.columns
                                      [:div.column.is-10>p.has-text-justified k]
                                      [:div.column>button.button.is-info.is-light
                                       {:on-click #(swap! keywords disj k)} "X"]]])
                              @keywords)])]
    [:div.column>div.columns
     [:div.column.is-four-fifths>div.card>div.card-content
       {:on-mouse-up on-mouse-up-event}
      (parser/parse section)]
     [:div.column
      [keywords-list]]]))

(defn workspace []
  [:div.columns>div.column.is-8.is-offset-2
   (workspace-title)
   [:div.columns
    [:div.column.is-four-fifths>p "HTML viewer"]
    [:div.column>p "keywords viewer"]]
   (if-let [contents  @(rf/subscribe [::subs/contents])]
     (map #(view-section %) (:article contents)))])

(defn main []
  [:div
   (navbar)
   (workspace)])
