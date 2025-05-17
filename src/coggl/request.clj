(ns coggl.request
  (:require [coggl.util :as util]
            [clj-http.client :as http]
            [coggl.url :as url]
            [jsonista.core :as json]
            [clojure.string :as str]))

(defn encode-auth-header [token]
  (->>
   (str/join [token ":api_token"])
   (util/str->b64)
   (str "Basic ")))

(defn decode-body [response]
  (cond
    (and
     (some? (get-in response [:body]))
     (str/starts-with? 
      (get-in response [:headers "Content-Type"])
      "application/json"))
    (assoc response
           :body (json/read-value
                           (:body response)
                           json/keyword-keys-object-mapper))
    :else response))

(defn wrap-headers [request]
  (let [token (:token request)]
   (-> request
       (dissoc :token)
       (assoc :headers
              (-> (:headers request)
                  (assoc "Content-Type" "application/json")
                  (assoc "Authorization" (encode-auth-header token))))
       (dissoc :token))))
      

(defn encode-body [request]
   (cond (some? (:body request))
     (let [value (json/write-value-as-string
                  (:body request)
                  json/keyword-keys-object-mapper)]
       (assoc request :body value))
     :else request))

(defn wrap-method [request]
  (let [method ((:method request) {:get http/get
                                   :post http/post})]
    (assoc request :method method)))

(defn call-api [request]
  (let [url (:url request) method (:method request)]
    (->> (-> request
          (dissoc :url)
          (dissoc :method))
        (method url))))

(defn make-request [request]
  (-> request
      (url/wrap-url-params)
      (wrap-headers)
      (encode-body)
      (wrap-method)
      (call-api)
      (decode-body)))

