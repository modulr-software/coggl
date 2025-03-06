(ns coggl.fs
  (:require [babashka.fs :as fs]))

(defn has-previous-session? [path]
  (fs/regular-file? path))

(defn write-session-file [path content]
  (when-not (has-previous-session? path)
    (fs/create-file path))
  (fs/update-file path (fn [_] content)))

(defn read-session-file [path]
  (-> path
   (fs/read-all-lines)
   (clojure.string/join)))


