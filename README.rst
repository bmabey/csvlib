================================
csvlib - CSV library for Clojure
================================

About
=====
Library to read and write CSV files with Clojure.

Usage
=====

Reading
-------
Assume `log.csv` has the following data

::

    Date,IP
    2/20/11 7:58 AM,1.0.1.0

Then you can do

::

    ; Simple use
    (let [records (read-csv "log.csv")]
      (println ((first records) 0)))

    ;=> Date

    ; With headers
    (let [records (read-csv "log.csv" :headers? true)]
      (println ((first records) "IP")))

    ;=> 1.0.1.0

    ; With conversion
    (let [df (DateFormat/getInstance)
          convert {"Date" #(.parse df %)}
          records (read-csv "log.csv" :headers? true :convert convert)]
      (println ((first records) "Date")))
    
    ;=> #<Date Sun Feb 20 07:58:00 PST 2011>

Writing
-------

::

    ; Simple usage
    (write-csv [[1 2 3] [4 5 6]] "points.csv")

    ; With headers
    (write-csv [[1 2 3] [4 5 6] "points.csv" :headers ["x" "y" "z"])

    ; Write maps, first map will be used as headers information
    (write-csv [{"x" 1 "y" 2 "z" 3} {"x" 4 "y" 5 "z" 6}] "points.csv")

    ; Use a formatter
    (def format { "x" : #(str (* % 2)) })
    (write-csv [{"x" 1 "y" 2 "z" 3} {"x" 4 "y" 5 "z" 6}] "points.csv"
                :format format)

    ; Use a different delimiter
    (write-csv [[1 2 3] [4 5 6]] "points.csv" :delimiter \|)

License
=======
Copyright (C) 2010 Miki Tebeka <miki.tebeka@gmail.com>

Distributed under the Eclipse Public License, the same as Clojure.
