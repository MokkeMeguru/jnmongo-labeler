(ns jnmongo-labeler.frontend.events
  (:require
   [re-frame.core :as rf]
   [jnmongo-labeler.frontend.db :as db]
   [day8.re-frame.http-fx]
   [ajax.core :as ajax]
   ))


(rf/reg-event-db
 ::initialize-db
 db/default-db)


(rf/reg-event-fx
 ::load-static-file
 (fn [{:keys [db]} [_ _]]
   {:http-xhrio {:method :get
                 :uri (str "./sample.json")
                 :timeout 2000
                 :response-format (ajax/json-response-format {:keywords? true})
                 :on-success [::resource-get-success]
                 :on-failure [::resource-get-failed]}}))

(rf/reg-event-db
 ::resource-get-success
 (fn [db [_ response]]
   (js/console.log "response" response)
   (-> db
       (assoc :contents response))))

(rf/reg-event-db
 ::resource-get-failed
 (fn [db [_ response]]
   (-> db
       (assoc :error response))))
