(ns coggl.toggl
  (:require [clj-http.client :as http]
            [buddy.core.codecs.base64 :as b]
            [buddy.core.codecs :as cod]
            [jsonista.core :as json]))

(defn str->b64 [token]
  (->
   (b/encode token)
   (cod/bytes->str))
  )
   

(defn encode-header [token]
  (->>
   (clojure.string/join [token ":api_token"])
   (str->b64)
   (str "Basic ")))

(defn decode-body [response]
  (assoc
   response
   :body
   (json/read-value
    (:body response)
    json/keyword-keys-object-mapper)))

(def api {:me "https://api.track.toggl.com/api/v9/me"
          :workspace "https://api.track.toggl.com/api/v9/workspaces/{workspace_id}"})

(clojure.string/replace (:workspace api) "{workspace_id}" (str 123))

(defn wrap-headers [request token]
  (-> request
      (assoc :headers
             (-> (:headers request)
                 (assoc "Content-Type" "application/json")
                 (assoc "Authorization" (encode-header token))))))

(defn get-me [token]
  (->
   (http/get
    (:me api)
    (-> {}
        (wrap-headers token)))
   (decode-body)))

(defn get-workspace [id token]
  (->
   (clojure.string/replace (:workspace api) "{workspace_id}" (str id))
   (http/get (wrap-headers {} token))
   (decode-body)))

(defn get-projects [workspace_id token]
  )

