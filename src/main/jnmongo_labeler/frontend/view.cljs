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

(def title (r/atom ""))
(defn workspace-title []
  [:div.columns>div.column.is-full
   [:div>h2 "title > " (if @(rf/subscribe [::subs/contents]) @title)]
   [:div.columns
    [:div.column.field.has-addons
     [:div.control
      [:input.input.is-small
       {:type "text" :name "titles" :list "titles"
        :placeholder "not selected"
        :on-change #(do
                      (println "update" (-> % .-target .-value))
                      (reset! title (-> % .-target .-value)))}]
      [:datalist#titles
       (map #(do [:option (str %)])
            @(rf/subscribe [::subs/titles]))]]
     [:div.control
      [:a.button.is-info.is-small
       {:on-click #(rf/dispatch-sync [::events/load-contents @title])}
       "Load"]]]
    [:div.column.is-3
     (if (-> @(rf/subscribe [::subs/titles]) count zero?)
       [:button.button.is-info.is-small
        {:on-click #(rf/dispatch-sync [::events/load-title-list])} "load titles"]
       [:button.button.is-dark.is-small
        {:on-click #(rf/dispatch-sync [::events/load-title-list])} "reload titles"])]]])

(defn getSelected []
  (cond
    (js/window.getSelection)
    ["get" (js/window.getSelection)]
    (js/document.getSelection)
    ["get" (js/document.getSelection)]
    (js/document.selection)
    ["range" (.. js/document -selection createRange)]
    :else nil))

(defn view-section [idx child-titles section]
  (let [original section
        keywords (r/atom (set []))
        on-mouse-up-event (fn [] (when-let [[type selection] (getSelected)]
                                  (swap! keywords conj (str selection))))
        keywords-list (fn []
                        [:div.box {:style {:max-height "300px"
                                            :overflow-y "auto"
                                            :overflow-x "auto"}}
                         [:ul
                          (map (fn [k]
                                 [:li
                                  [:div.columns.is-vcentered
                                   [:div.column.is-9>p.has-text-justified k]
                                   [:div.column.is-3>button.button.is-info.is-light.is-small
                                    {:on-click #(swap! keywords disj k)} "X"]]])
                               @keywords)]])
        send-button (fn []
                      [:div.column.has-text-centered>a.button
                       {:on-click #(rf/dispatch-sync
                                    [::events/set-candidates
                                     {:content_idx idx :candidates @keywords}])}
                       "Send to server"])]
    [:div.column {:keys idx}
     [:div.columns
      [:div.column.is-8>div.card>div.card-content
       {:on-mouse-up on-mouse-up-event}
       [:div.colum
        [:p {:style {:padding "0 0.5rem"
                     :display "inline-block" :border-bottom "solid 0.1rem white"}}
         (clojure.string/join ">" child-titles)]]
       (parser/parse section)]
      [:div.column
       [keywords-list]
       [send-button]]]]))

(defn workspace []
  [:div.columns>div.column.is-8.is-offset-2
   (workspace-title)
   [:div.columns
    [:div.column.is-four-fifths.has-text-centered>p "HTML viewer"]
    [:div.column>p "keywords viewer"]]
   (when-let [contents  @(rf/subscribe [::subs/contents])]
     (map #(view-section (:idx %) (:child_titles %) (:contents %)) (:article contents)))])

(defn main []
  [:div
   (navbar)
   (workspace)])
