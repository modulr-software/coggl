(ns coggl.crypt-fs
  (:require
   [babashka.fs :as fs]
   [coggl.password :as crypt]))

(defn write-file-crypt [path password content]
  (let [ciphertext (crypt/hash-value content password)]
    (fs/update-file path (fn [_] ciphertext))))

(defn read-file-crypt [path password]
  (let [ciphertext (->
                    (fs/read-all-lines path)
                    (clojure.string/join))]
    (crypt/verify ciphertext password)))
