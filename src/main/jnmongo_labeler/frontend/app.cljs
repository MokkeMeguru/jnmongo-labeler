(ns jnmongo-labeler.frontend.app
  (:require [jnmongo-labeler.utils.parser :as p]
            [re-frame.core :as rf]
            [reagent.core :as r]
            [reagent.dom :as rd]
            [jnmongo-labeler.frontend.view :as view]))

(defn ^:dev/after-load mount-root []
  (rf/clear-subscription-cache!)
  (rd/render [view/main]
            (js/document.getElementById "app")))


(defn init []
  (println "Hello World")
  (println (p/greeting))
  (js/console.log "Hello World" (p/greeting))
  (mount-root))
