(ns coggl.url)
  
(def api {:me "https://api.track.toggl.com/api/v9/me"
          :workspace "https://api.track.toggl.com/api/v9/workspaces/{workspace_id}"
          :clients "https://api.track.toggl.com/api/v9/workspaces/{workspace_id}/clients"
          :summary-report "https://api.track.toggl.com/reports/api/v3/workspace/{workspace_id}/summary/time_entries.pdf"
          :time-entries "https://api.track.toggl.com/api/v9/me/time_entries"})

(defn- inject [url key value]
  (clojure.string/replace url (str "{" (name key) "}") (str value)))

(defn- wrap-params-recur [url params]
  (let [keys (-> params (keys) (vec))]
    (cond
      (> (count keys) 0)
      (let [k (first keys)
            parsed-url (inject url k (get-in params [k]))]
        (->> (dissoc params k)
             (wrap-params-recur parsed-url)))
      :else url)))

(defn wrap-url-params [request]
  (let [url ((:url-key request) api)
        params (:url-params request)]
    (cond
      (some? url)
      (-> request
          (assoc :url (wrap-params-recur url params))
          (dissoc :url-key :url-params)) 
      :else
      request)))
  