(ns jnmongo-labeler.frontend.subs
  (:require [re-frame.core :as rf]))


(rf/reg-sub
 ::contents
 (fn [db]
   (:contents db)))



(rf/reg-sub
 ::error
 (fn [db]
   (:error db)))


(rf/reg-sub
 ::titles
 (fn [db]
   (:titles db)))
