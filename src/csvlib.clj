(ns csvlib
  (:import (com.csvreader CsvReader CsvWriter)
            java.nio.charset.Charset))

(defn- make-converter
  "Make a converter function from a conversion table"
  [converison]
  (let [convert (fn [key value] ((get converison key identity) value))]
    (fn [record]
      (zipmap (keys record) (map #(convert % (record %)) (keys record))))))

; Default delimiter
(def *delimiter* \,)
; Default charset
(def *charset* "UTF-8")

(defn- record-seq 
  "Reutrn a lazy sequence of records from a CSV file"
  [filename delimiter charset]
  (let [csv (CsvReader. filename delimiter (Charset/forName charset))
        read-record (fn [] 
                      (when (.readRecord csv) 
                        (into [] (.getValues csv))))]
    (take-while (complement nil?) (repeatedly read-record))))

(defn read-csv
  "Return a lazy sequence of records (maps) from CSV file.

  With header map will be header->value, otherwise it'll be position->value.
  `conversions` is an optional map from header to a function that convert the
  value.
  
  Additional keyword arguments are 'charset' for the file character set and
  'delimiter' for record delimiter"
  [filename & {:keys [headers? conversion charset delimiter]
               :or {charset *charset* delimiter *delimiter*}}]
   (let [records (record-seq filename delimiter charset)
         convert (make-converter conversion)
         headers (if headers? (first records) (range (count (first records))))]
     (map convert
          (map #(zipmap headers %) (if headers? (rest records) records)))))

