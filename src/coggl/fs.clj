(ns coggl.fs
  (:require [babashka.fs :as fs]
            [buddy.core.codecs :as cod]
            [coggl.config :as conf]
            [clojure.string :as str]))

;;todo --- we need to create an env validation function using a schema

(defn get-config-dir []
  (str
   (System/getenv "HOME")
   "/"
   ".coggle"))

(defn get-email-template-file []
  (let [path (:invoice-template-file-path conf/env)]
    (if (some? path)
      (java.io.File. path)
      nil)))

(defn has-password-file? []
  (let [path (get-in conf/env [:email-password-file-path])]
    (if (and (some? path)
             (fs/readable? path))
      path
      nil)))

(defn read-gmail-password []
  (when-let [path (has-password-file?)]
    (let [value (-> (slurp (:email-password-file-path conf/env))
                    (str/split #"\n")
                    (first))]
      (if (< 0 (count value))
        value
        nil))))

(defn has-config-dir? [path]
  (fs/directory? path))

(defn write-report! [name content]
  (babashka.fs/write-bytes name (cod/str->bytes content)))

(defn write-reports!
  ([contents options]
   (let [extension (:extension options)
         dir (or
              (:dir options)
              (get-config-dir))]
     (->> (vec (keys contents))
          (mapv (fn [client]
                  (when (not (has-config-dir? dir))
                    (fs/create-dir dir))
                  (write-report!
                   (str
                    (if (some? dir) (str dir "/") "")
                    (name client)
                    "."
                    (name (or extension :pdf)))
                   (client contents)))))))
  ([contents]
   (write-reports! contents {:extension :pdf
                             :dir (get-config-dir)})))

(comment
  (write-reports! {:turnstay "some test content"
                   :simply "some other text"} {:extension "txt" :dir (get-config-dir)}))

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


