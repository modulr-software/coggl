(ns coggl.invoice.components
  (:require [coggl.invoice.util :as util]))

(defn invoice
  [& {:keys [company-name
             invoice-for
             company-address
             company-contact
             company-banking
             entries             ;; [{:description \"...\" :quantity  n :unit-price  p :total-price  t} …]
             adjustments
             due-date
             invoice-number]}]
  (let [subtotal (reduce + (map :total-price entries))
        total    (- subtotal adjustments)]
    [:html {:lang "en"}
     [:head
      [:meta {:charset "UTF-8"}]
      [:title (str "Invoice " invoice-number)]
      ;; inline CSS placeholder — invoice-css must be bound to your stylesheet string
      [:style {:type "text/css"} (util/invoice-css)]]
     [:body
      [:div.invoice-container

       ;; Header
       [:header.header
        [:h1.invoice-number invoice-number]
        [:div.banking-details
         [:h2 company-name]
         [:p company-address]
         [:p company-banking]
         [:p.contact company-contact]]]

       ;; VAT note
       [:p.vat-note "not a registered VAT vendor"]

       ;; Info grid: three columns
       [:section.info-grid
        [:div
         [:strong "Invoice for"] [:br]
         invoice-for]
        [:div
         [:strong "Invoice #"] [:br]
         invoice-number]
        [:div
         [:strong "Due date"] [:br]
         due-date]]

       ;; Line items
       [:table.items
        [:thead
         [:tr
          [:th "Description"]
          [:th "Qty (h)"]
          [:th "Unit Price"]
          [:th "Total Price"]]]
        [:tbody
         (for [{:keys [description quantity unit-price total-price]} entries]
           [:tr
            [:td description]
            [:td.right (str quantity)]
            [:td.right (str unit-price)]
            [:td.right (str total-price)]])]]

       ;; Summary totals (table layout)
       [:div.summary-notes
        [:div.totals
         [:div.total-row
          [:span.total-label "Subtotal"]
          [:span.total-value (str subtotal)]]
         [:div.total-row
          [:span.total-label "Adjustments"]
          [:span.total-value (str adjustments)]]
         [:div.total-row.total
          [:span.total-label "Total"]
          [:span.total-value (str total)]]]]

       ;; Footer
       [:footer "not a registered VAT Vendor"]]]]))

