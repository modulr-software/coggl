(ns coggl.crypt-fs
  (:require
   [babashka.fs :as fs]
   [coggl.password :as crypt]))

(defn write-file-crypt [path password content]
  (let [ciphertext (crypt/hash-value content password)]
    (fs/write-bytes path (.getBytes (String. ciphertext)))))

(defn read-file-crypt [path password]
  (-> (slurp path)
      (crypt/verify  password)))
