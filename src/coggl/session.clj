(ns coggl.session
  (:require [coggl.fs :as fs]
            [coggl.password :as pw]
            [coggl.toggl :as toggl]))

(defn get-token [path password]
  (-> (fs/read-session-file path)
      (pw/verify password)
      (or nil)))

(defn save-token! [path token password]
  (->> (pw/hash-value token password)
   (fs/write-session-file path)))

(defn delete-token! [path]
  (babashka.fs/delete-if-exists path))


(defn validate-session [path password]
  (let [token (get-token path password)]
  (cond
    (not (some? token))
    false
    :else
    (or (toggl/get-me token) nil))))