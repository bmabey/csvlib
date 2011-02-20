================================
csvlib - CSV library for Clojure
================================

About
=====
Library to read and write CSV files with Clojure.

Usage
=====
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

FAQ
===
Q: Will there be support for writing?
A: Yes, later

License
=======
Copyright (C) 2010 Miki Tebeka <miki.tebeka@gmail.com>

Distributed under the Eclipse Public License, the same as Clojure.
