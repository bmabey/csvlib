================================
csvlib - CSV library for Clojure
================================

About
=====
Library to read and write CSV files with Clojure.

Usage
=====
::

    ; Simple use
    (let [records (read-csv "log.csv")]
      (println ((first records) 0)))

    ; With headers
    (let [records (read-csv "log.csv" :headers? true)]
      (println ((first records) "IP")))

    ; With conversion
    (let [df (DateFormat/getInstance)
          convert {"Date" #(.parse df %)}
          records (read-csv "log.csv" :headers? true :convert convert)]
      (println ((first records) "Date")))

FAQ
===
Q: Will there be support for writing?
A: Yes, later

License
=======
Copyright (C) 2010 Miki Tebeka <miki.tebeka@gmail.com>

Distributed under the Eclipse Public License, the same as Clojure.
