(ns coggl.toggl
  (:require [coggl.request :as req]
            [coggl.fs :as files]
            [coggl.util :as util]
            [coggl.fs :as fs]))

(defn get-me [token]
  (-> {:url-key :me :method :get :token token}
      (req/make-request)
      (:body)))
         
(defn get-default-workspace-id [token]
  (-> (get-me token)
      (:default_workspace_id)))
 
(defn get-client-ids-recur
  ([clients] (get-client-ids-recur clients {}))
  ([clients m]
   (if (some? (first clients))
     (get-client-ids-recur
      (subvec clients 1)
      (assoc m
             (keyword (:name (first clients)))
             (:id (first clients))))
     m)))

(defn get-client-ids [token]
  (-> (req/make-request
       {:method :get
        :url-key :clients
        :token token
        :url-params {:workspace_id (get-default-workspace-id token)}})

      (:body)
      (get-client-ids-recur)))

(defn get-default-summary-report [token]
  (->>
   (req/make-request {:method :post
                      :url-key :summary-report
                      :token token
                      :url-params {:workspace_id (get-default-workspace-id token)}
                      :body {
                             :start_date "2025-01-01"
                             :end_date "2025-01-31"
                             :client_ids (vec (vals (get-client-ids token)))
                      }})
   (:body)
   (files/write-report! "test.pdf")
   ))

(defn get-summary-report-for-client [params]
  (let [token (:token params)
        client (:client params)
        all-client-ids (get-client-ids token)
        workspace_id (get-default-workspace-id token)]
    (->>
     (when (some? (client all-client-ids))
       (try
       (req/make-request {:url-key :summary-report
                          :token token
                          :method :post
                          :url-params {:workspace_id workspace_id}
                          :body {:start_date "2025-01-01"
                                 :end_date  "2025-01-31"
                                 :client_ids [(client all-client-ids)]}})
         (catch Exception _
           {:body nil}))
         )
     (:body)
     (assoc {} client))))


(comment
  (do
    (when (not (fs/has-config-dir? "/Users/merv/.test"))
      (babashka.fs/create-dir "/Users/merv/.test"))
    (->>
     (get-summary-report-for-client {:token (util/read-dev-token)
                                     :client :gamify})

    ;;  (mapv (fn [[k v]] ()))
    ;;  (:gamify)
    ;;  (fs/write-report! "/Users/merv/.test/test.pdf")
     )))

(defn get-summary-report-for-clients [& {:keys [token clients] :as _params}]
  (let [all-client-ids (get-client-ids token)
        workspace_id (get-default-workspace-id token)]
    (->>
     (mapv (fn [v] (v all-client-ids)) clients)
     (mapv (fn [client_id]
             (req/make-request {:url-key :summary-report
                                :method :post
                                :url-params {:workspace_id workspace_id}
                                :body {
                                       :start_date "2025-02-01"
                                       :end_date "2025-02-28"
                                       :client_ids [client_id]
                                }
                                :token token})))
     (mapv (fn [v] (:body v)))
     (zipmap clients))))

(comment
  (get-client-ids ())
  (->>
  (get-summary-report-for-clients {
                                   :token (util/read-dev-token)
                                   :client [:turnstay :simply]})
   (coggl.fs/write-reports!)
;;   (first)
;;   (coggl.fs/write-report "specific.pdf")
   ))


(comment
  ;; how to make a request
  (try
    (req/make-request {:token (util/read-dev-token)
                       :method :get
                       :url-key :time-entries
                       :url-params {}
                       :body {:start_date "2025-01-01"
                              :end_date "2025-01-31"
                              :workspace_id 8731338}})
    (catch Exception e
      e))
  )