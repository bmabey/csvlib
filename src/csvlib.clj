(ns csvlib
  (:import (com.csvreader CsvReader CsvWriter)
            java.nio.charset.Charset)
  (:use [clojure.set :only (subset?)]))

; Default delimiter
(def *delimiter* \,)
; Default charset
(def *charset* "UTF-8")
; Flush every record write?
(def *flush?* nil)

(defn- make-converter
  "Make a converter function from a conversion table."
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

(defn read-csv
  "Return a lazy sequence of records (maps) from CSV file.

  With headers? map will be header->value, otherwise it'll be position->value.
  
  Options keyword arguments:
    headers? - Use first line as headers
    convert - A conversion map (field -> conversion function)
    charset - Charset to use (defaults to *charset*)
    delimiter - Record delimiter (defaults to *delimiter*)"
  [filename & {:keys [headers? convert charset delimiter]
               :or {charset *charset* delimiter *delimiter*}}]
   (let [records (record-seq filename delimiter charset)
         convert (make-converter convert)
         headers (if headers? (first records) (range (count (first records))))]
     (map convert
          (map #(zipmap headers %) (if headers? (rest records) records)))))

(defn vectorize-headers 
  "Return a vector of headers keys sorted by values"
  [headers]
  (vec (map first (sort-by headers headers))))

(defn- gen-headers 
  "Generate headers for combinations of headers supplied by the user (which can
  be a nil, map or a vector, and the first record (which can also be nil, map or
  vector)."
  [headers record]
  (cond
    (and (nil? headers) (nil? record)) nil
    (and (nil? headers) (vector? record)) nil
    (vector? headers) headers
    (map? headers) (vectorize-headers headers)
    (map? record) (vectorize-headers record)))

(defn- keyset
  "Return a set of map keys."
  [map]
  (set (keys map)))

(defn- unknowns? 
  "Return true if there are any unknown fields in record."
  [record headers]
  (or (> (count record) (count headers))
      (not (subset? (keyset record) (set headers)))))

(defn- write-values 
  "Write values (a line) to a CSV, will flush if *flush?* is true."
  [writer values]
  (.writeRecord writer (into-array String values))
  (when *flush?* (.flush writer)))

(defn- gen-formatter 
  [format headers]
  (let [index (if headers #(headers %) identity)]
    (fn [record]
      (keep-indexed #((get format (index %1) str) %2) record))))

(defn- sort-record 
  "Sort record by headers. Return a sequence of values."
  [record headers]
  (when (unknowns? record headers) (throw (Exception. "unknown fields")))
  (map #(get record % nil) headers))

(defn- gen-values 
  "Generate values seq from a record."
  [record headers format]
  (when (and (map? record) (nil? headers)) 
    (throw (Exception. "map record with no headers")))
  (let [record (if (vector? record) record (sort-record record headers))]
    (format record)))

(defn write-csv
  "Write records to CSV.

  Optional arguments are:
    delimiter - Delimiter to use (defaults to *delimiter*)
    charset - Charset to use (default to *charset*)
    flush? - Flag to flush after every record (default to *flush?*)
    format - A map key -> formatter"
  [records filename & {:keys [delimiter charset headers flush? format]
                       :or {delimiter *delimiter* charset *charset*}}]
  (let [writer (CsvWriter. filename delimiter (Charset/forName charset))
        headers (gen-headers headers (first records))
        formatter (gen-formatter format headers)]
    (binding [*flush?* flush]
      (when headers (write-values writer headers))
      (dorun 
        (map #(write-values writer (gen-values % headers formatter)) records))
      (.close writer))))
