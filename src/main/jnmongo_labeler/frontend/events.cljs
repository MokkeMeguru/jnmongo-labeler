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
 ::set-candidates
 (fn [{:keys [db]} [_ kvs]]
   {:http-xhrio {:method :put
                 :uri (str "http://localhost:8085/set_candidates")
                 :timeout 2000
                 :params kvs
                 :response-format (ajax/json-response-format
                                   {:keywords? true})
                 :format (ajax/json-request-format)
                 :on-success [::set-candidate-success]
                 :on-failure [::resource-get-failed]}}))

(rf/reg-event-db
 ::set-candidate-success
 (fn [db [_ response]]
   db))

(rf/reg-event-fx
 ::load-title-list
 (fn [{:keys [db]} [_ _]]
   {:http-xhrio {:method :get
                 :uri (str "http://localhost:8085/list")
                 :timeout 2000
                 :response-format (ajax/json-response-format
                                   {:keywords? true})
                 :on-success [::titles-get-success]
                 :on-failure [::resource-get-failed]}}))

(rf/reg-event-fx
 ::load-contents
 (fn [{:keys [db]} [_ val]]
   {:http-xhrio {:method :get
                 :uri (str "http://localhost:8085/contents")
                 :params {:doc_title val}
                 :format (ajax/json-request-format)
                 :timeout 2000
                 :response-format (ajax/json-response-format
                                   {:keywords? true})
                 :on-success [::contents-get-success]
                 :on-failure [::resource-get-failed]}}))


(rf/reg-event-db
 ::contents-get-success
 (fn [db [_ response]]
   (println response)
   (println (-> response :body second :contents))
   (-> db
       (assoc :contents {:article (-> response :body)}))))


(rf/reg-event-db
 ::titles-get-success
 (fn [db [_ response]]
   (println "titles" (-> response :body))
   (-> db
       (assoc :titles (-> response :body)))))


(rf/reg-event-fx
 ::load-static-file
 (fn [{:keys [db]} [_ _]]
   {:http-xhrio {:method :get
                 :uri (str "./sample.json")
                 :timeout 2000
                 :response-format (ajax/json-response-format
                                   {:keywords? true})
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
