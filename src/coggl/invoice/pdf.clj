(ns coggl.invoice.pdf
  (:require [etaoin.api :as e]
            [coggl.invoice.components :as inv]))

(def mock-data {:total 19000
                :subtotal 19000
                :adjustments 0
                :due-date "immediate"
                :company-name "ModulR (Pty) Ltd"
                :invoice-for "Appsquare (Pty) Ltd"
                :invoice-number "APSQ001"
                :company-address "302 waterford place, camargue boulevard, macassar, cape town 7130"
                :company-banking "Account Type: Checking\nBank: Capitec Business\nAccount Nr: 1234567890\nAccount holder: ModulR (Pty) Ltd"
                :submission-date "05/05/2025"
                :company-contact "+26 84 254 8270"
                :entries [{:description "zohm react rebuild"
                           :quantity 80
                           :unit-price 250
                           :total-price 20000}]})

(def driver (e/chrome-headless))

(e/with-chrome-headless driver
  (e/go driver "https://www.youtube.com")
  (e/print-page driver "target/etaoin-play/printed.pdf"))
