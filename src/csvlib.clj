(ns csvlib
  (import com.csvreader.CsvReader))

(defn- record-seq [filename]
  (let [csv (CsvReader. filename)
        read-record (fn [] 
                      (when (.readRecord csv) 
                        (into [] (.getValues csv))))]
    (take-while (complement nil?) (repeatedly read-record))))

(defn read-csv
  "Return a lazy sequence of records (maps) from CSV file.

  With header map will be header->value, otherwise it'll be position->value."
  ([filename] (read-csv filename false))
  ([filename headers?]
   (let [records (record-seq filename)
         headers (if headers? (first records) (range (count (first records))))]
    (map #(zipmap headers %) (if headers? (rest records) records)))))
  
