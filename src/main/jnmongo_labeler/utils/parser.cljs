(ns jnmongo-labeler.utils.parser
  (:require  ;; [hickory.core :as h]
   [jnmongo-labeler.utils.hickory :as h]
            [hickory.convert :as hc]
            [hickory.utils :as utils]))


(defn- render-hickory-attribute
  "Given a map entry m, representing the attribute name and value, returns a
   string representing that key/value pair as it would be rendered into HTML."
  [m]
  (let [keyword (-> m key name)
        keyword (if (= keyword "colspan") "col-span" keyword)]
   (str " " keyword "=\"" (utils/html-escape (val m))  "\"")))

(defn hickory-to-html
  [dom]
  (if (string? dom)
    dom
    ;; (utils/html-escape dom)
    (case (keyword (:type dom))
      :document
      (apply str (map hickory-to-html (:content dom)))
      :document-type
      (utils/render-doctype (get-in dom [:attrs :name])
                            (get-in dom [:attrs :publicid])
                            (get-in dom [:attrs :systemid]))
      :element
      (cond
        (utils/void-element (keyword (:tag dom)))
        (str "<" (name (keyword (:tag dom)))
             (apply str (map render-hickory-attribute (:attrs dom)))
             ">")
        (utils/unescapable-content (keyword (:tag dom)))
        (str "<" (name (keyword (:tag dom)))
             (apply str (map render-hickory-attribute (:attrs dom)))
             ">"
             (apply str (:content dom)) ;; Won't get html-escaped.
             "</" (name (keyword (:tag dom))) ">")
        :else
        (str "<" (name (keyword (:tag dom)))
             (apply str (map render-hickory-attribute
                             (:attrs (update-in dom [:attrs] dissoc :style))))
             ">"
             (apply str (map hickory-to-html (:content dom)))
             "</" (name (keyword (:tag dom))) ">"))
      :comment
      (str "<!--" (apply str (:content dom)) "-->")
      (str dom))
    ))


(defn greeting []
  (println "code" (h/parse "<p>Hello hickory via browser-repl</p>")) 
  (h/parse "<p>Hello hickory via browser-repl</p>"))


(defn parse [hick]
  (println "tag " hick)
  (println "hiccup" (->
                     (h/as-hiccup (h/parse
                                   (if (map? hick)
                                     (hickory-to-html hick)
                                     (hickory-to-html {:type :document :content hick}))))
                     first
                     last))
  (->
   (h/as-hiccup (h/parse
                 (if (map? hick)
                   (hickory-to-html hick)
                   (hickory-to-html {:type :document :content hick}))))
   first
   last
   last))
