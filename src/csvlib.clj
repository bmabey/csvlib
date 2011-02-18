(ns csvlib
  (import com.csvreader.CsvReader))

(defn make-converter
  "Make a converter function from a conversion table"
  [converison]
  (let [convert (fn [key value] ((get converison key identity) value))]
    (fn [record]
      (zipmap (keys record) (map #(convert % (record %)) (keys record))))))

(defn- record-seq 
  "Reutrn a lazy sequence of records from a CSV file"
  [filename]
  (let [csv (CsvReader. filename)
        read-record (fn [] 
                      (when (.readRecord csv) 
                        (into [] (.getValues csv))))]
    (take-while (complement nil?) (repeatedly read-record))))

(defn read-csv
  "Return a lazy sequence of records (maps) from CSV file.

  With header map will be header->value, otherwise it'll be position->value.
  `conversions` is an optional map from header to a function that convert the
  value."
  ([filename] (read-csv filename false nil))
  ([filename headers?] (read-csv filename headers? nil))
  ([filename headers? conversion]
   (let [records (record-seq filename)
         convert (make-converter conversion)
         headers (if headers? (first records) (range (count (first records))))]
     (map convert
          (map #(zipmap headers %) (if headers? (rest records) records))))))
  
