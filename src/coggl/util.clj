(ns coggl.util
  (:require [coggl.fs :as fs]))

(defn read-dev-token []
  (fs/read-session-file "dev.token"))

(comment
  (read-dev-token))