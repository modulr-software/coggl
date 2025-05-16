(ns coggl.util
  (:require [coggl.fs :as fs]
            [coggl.config :as config]
            [buddy.core.codecs :as cod]
            [buddy.core.codecs.base64 :as b]))

(defn read-dev-token []
  (->
   (:dev-token-path config/env)
   (fs/read-session-file)))

(defn str->b64 [token]
  (->
   (b/encode token)
   (cod/bytes->str)))

(defn prr [v] (println v) v)
