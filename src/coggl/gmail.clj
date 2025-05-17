(ns coggl.gmail
  (:require [postal.core :as postal]
            [hiccup.page :as h]
            [coggl.fs :as fs]
            [coggl.config :as conf]))

(defn send-invoice [& {:keys [file to] :as _opts}]
  (let [{:keys [email-username]} conf/env
        gmail-password (fs/read-gmail-password)]
    (prn gmail-password)
    (postal/send-message
     {:host "smtp.gmail.com"
      :user email-username
      :pass gmail-password
      :port 587
      :tls true}
     {:from email-username
      :to to
      :subject "modulr invoice"
      :body [{:type "text/plain"
              :content "Hi client name, please find attached the invoice for the month of xxx"}
             {:type :attachment
              :content file}]})))

(comment
  (h/include-css "filename"))
