(ns csvlib
  (:import (com.csvreader CsvReader CsvWriter)
            java.nio.charset.Charset))

(defn- make-converter
  "Make a converter function from a conversion table"
  [converison-map]
  (let [convert (fn [key value] ((get converison-map key identity) value))]
    (fn [record]
      (zipmap (keys record) (map #(convert % (record %)) (keys record))))))

(defn- record-seq 
  "Reutrn a lazy sequence of records from a CSV file"
  [filename delimiter charset]
  (let [csv (CsvReader. filename delimiter (Charset/forName charset))
        read-record (fn [] 
                      (when (.readRecord csv) 
                        (into [] (.getValues csv))))]
    (take-while (complement nil?) (repeatedly read-record))))

; Default delimiter
(def *delimiter* \,)
; Default charset
(def *charset* "UTF-8")

(defn read-csv
  "Return a lazy sequence of records (maps) from CSV file.

  With header map will be header->value, otherwise it'll be position->value.
  `conversions` is an optional map from header to a function that convert the
  value."
  [filename & {:keys [delimiter charset headers? convert]
               :or {delimiter *delimiter* charset *charset*}}]
   (let [records (record-seq filename delimiter charset)
         convert (make-converter convert)
         headers (if headers? (first records) (range (count (first records))))]
     (map convert
          (map #(zipmap headers %) (if headers? (rest records) records)))))

(defn- indexes [coll]
  (range (count coll)))

(defn- gen-headers 
  "Generate headers for combinations of headers supplied by the user (which can
  be a nil, map or a vector, and the first record (which can also be nil, map or
  vector)."
  [headers record]
  (cond
    (and (nil? headers) (nil? record)) nil
    (and (nil? headers) (vector? record)) nil
    (vector? headers) (zipmap headers (indexes headers))
    (map? headers) headers
    (map? record) (zipmap (keys record) (indexes record))))

(defn sort-record [record headers]
  (if (vector? record)
    record
    (if headers
      (sort-by :record headers)
      (assert aaaa
      record)))

; (defn write-csv
;   [records filename & {:keys [delimiter charset headers]
;                :or {delimiter *delimiter* charset *charset*}}]
;   (let [writer (CsvWriter. filename delimiter (Charset/fromName charset))
;         headers (gen-headers headers (first records))]
;     (when headers (



; (defn write-csv
;   ([filename] (write-csv filename records nil))
;   [filename records opts]
;   (let [delimiter (:delimiter opts \,)
;         charset (Charset/forName (:charset opts "UTF-8"))
;         headers (gen-headers (:headers opts) (first records))]
;   (let [writer (CsvWriter. filename delimiter (Charset/fromName charset))
;         headers? (not (nil? headers))
;         header 
;         headers+ (if headers headers (zipmap (range

