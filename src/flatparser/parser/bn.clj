
(ns flatparser.parser.bn
  (:refer-clojure)
  (:use [net.cgrand enlive-html]
        [clojure pprint repl]
        [clojure.java io]
        [flatparser geo util]
        [flatparser.parser putil]))


(def subway-stations
  ["Девяткино"
   "Гражданский проспект"
   "Академическая"
   "Политехническая"
   "Площадь Мужества"
   "Лесная"
   "Выборгская"
   "Площадь Ленина"
   "Чернышевская"
   "Площадь Восстания"
   "Владимирская"
   "Пушкинская"
   "Технологический институт"
   "Балтийская"
   "Нарвская"
   "Кировский завод"
   "Автово"
   "Ленинский проспект"
   "Проспект Ветеранов"
   "Парнас"
   "Проспект Просвещения"
   "Озерки"
   "Удельная"
   "Пионерская"
   "Чёрная речка"
   "Петроградская"
   "Горьковская"
   "Невский проспет"
   "Сенная площадь"
   "Фрунзенская"
   "Московские ворота"
   "Электросила"
   "Парк Победы"
   "Московская"
   "Звёздная"
   "Купчино"
   "Приморская"
   "Василеостровская"
   "Гостиный двор"
   "Маяковская"
   "Площадь Александра Невского"
   "Елизаровская"
   "Ломоносовская"
   "Пролетарская"
   "Обухово"
   "Рыбацкое"
   "Спасская"
   "Достоевская"
   "Лиговский проспект"
   "Новочеркасская"
   "Ладожская"
   "Проспект Большевиков"
   "Улица Дыбенко"
   "Комендантский проспект"
   "Старая Деревня"
   "Крестовский остров"
   "Чкаловская"
   "Спортивная"
   "Адмиралтейская"
   "Садовая"
   "Звенигородская"
   "Обводной канал"
   "Волковская"
   ])


(def subway-coords-spb
  [[60.050205 30.442949] [60.034851 30.418741] [60.012874 30.396429] [60.008991 30.370525] [59.99975500000001 30.366003] [59.98503400000001 30.344209] [59.97126300000001 30.347309] [59.95726000000001 30.355493] [59.944515 30.359613] [59.930515 30.361183] [59.92773399999999 30.347822] [59.920787 30.329414] [59.91651899999999 30.31830399999999] [59.907436 30.29984799999999] [59.901023 30.274562] [59.87983699999999 30.261556] [59.867237 30.260854] [59.8507 30.268339] [59.841907 30.25144] [60.067325 30.334436] [60.05132200000001 30.333007] [60.037231 30.322092] [60.01664700000001 30.315128] [60.00255199999999 30.297283] [59.985458 30.30121] [59.96665900000001 30.311445] [59.956184 30.318735] [59.93514599999999 30.328575] [59.92707000000001 30.320438] [59.90620399999999 30.317972] [59.891521 30.318109] [59.87909699999999 30.319057] [59.86623299999999 30.32151199999999] [59.85122999999999 30.321071] [59.833335 30.349071] [59.82956599999999 30.375848] [59.948162 30.234458] [59.94267199999999 30.278678] [59.934238 30.335033] [59.93178099999999 30.354591] [59.923683 30.386198] [59.89662899999999 30.42374] [59.87707899999999 30.441785] [59.86514200000001 30.470022] [59.84848 30.457393] [59.830921 30.500619] [59.92704699999999 30.320379] [59.928333 30.34618699999999] [59.921012 30.354747] [59.92903899999999 30.41196] [59.932468 30.43915699999999] [59.919952 30.466922] [59.907295 30.483274] [60.00709029999999 30.2572269] [59.98972699999999 30.255014] [59.971591 30.259275] [59.96119599999999 30.291828] [59.95192300000001 30.291137] [59.935871 30.3152] [59.92669999999999 30.317327] [59.92240899999999 30.335533] [59.914775 30.34956099999999] [59.895771 30.3572]])


(def cookies
  (str "yandexuid=180027871345076714&"
       "PHPSESSID=9t8m4rb94sqqelei9aum3s45c2&"
       "__utma=1.880536119.1345076715.1345076715.1345076715.1&"
       "__utmb=1.2.9.1345076729632&"
       "__utmc=1&"
       "__utmz=1.1345076715.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none)&"
       "cid=Av5tznWJSDtr9rU4FTVxV0g==&"
       "_ym_visorc=b&"
       "yabs-sid=1422066671345076714&"
       "ruid=VF0ABuo9LFBwgwAAARKw+Q==&"
       "top100rb=ND12KzQ0Mis0NDM=&"
       "FTID=1GB3tg3FpIn0&"
       "VID=0-r7VQ2hSCn0&"
       "guid=7E5B0D03502C3DEAX1345076714"))


(def user-agent (str "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/535.11 "
                     "(KHTML, like Gecko) Chrome/17.0.963.56 Safari/535.11"))

(defn bn-make-resource [url-str]
  (let [url (java.net.URL. url-str)
        conn (.openConnection url)]
    (.setRequestProperty conn "Cookie" cookies)
    (.setRequestProperty conn "User-Agent" user-agent)
    (html-resource (java.io.InputStreamReader. (.getInputStream conn)
                                               "windows-1251"))))



(def param-names
  {:address "Адрес: "
   :room_no "Комнат: "
   :size_t "Общая площадь: "
   :size_l "Жилая площадь: "
   :size_k "Площадь кухни: "
   :floor "Этаж/ Этажность: "
   :floors "Этаж/ Этажность: "
   :price "Цена: "
   })


(defn parse-dist-to-subway [address]
  (if address
    (let [coords (coords-by-addr (str "Россия, Санкт Петербург, " address))]
      (if-not (empty? coords)
        (nearest-subway subway-coords-spb coords)
        NA))
    NA))
 
(defn param [p-map name]
  (p-map (param-names name)))


(defn parse-dist-to-kp [addr kp]
  (if kp
    (distance (coords-by-addr (str "Россия, Санкт-Петербург, " addr))
              (coords-by-addr (str "Россия, Санкт-Петербург, " kp)))
    NA))


(defn fetch-params
  "Takes parsed HTML resource of page with flat description
   map of additional information. 
   Returns map with found/calcuated params"
  [rsrc info]
  (let [names (map get-content
                   (select rsrc [:td (attr= :width "40%")]))
        values (map get-content (select rsrc [:td.str]))
        p-map (zipmap names values)
        coords (coords-by-addr (str "Россия, Санкт-Петербург, "
                                    (param p-map :address)))]
    {:price (parse-int (get-content (get-content (param p-map :price))))
     :address (param p-map :address)
     :lat (first coords)
     :lon (second coords)
     :dist_to_subway (parse-dist-to-subway (get-content
                                            (param p-map :address)))
     :dist_to_kp (parse-dist-to-kp (param p-map :address) (:kp info))
     :room_no (parse-int (param p-map :room_no))
     :size_t (parse-double (param p-map :size_t))
     :size_l (parse-double (param p-map :size_l))
     :size_k (parse-double (param p-map :size_k))
     :floor (parse-int (first (.split (param p-map :floor) "/")))
     :floors (parse-int (second (.split (param p-map :floor) "/")))     
     }))


(defn fetch-links [rsrc]
  (map #(str "http://www.bn.ru" %)
       (filter #(if % (.startsWith % "/detail/parend/") false)
               (map (comp :href :attrs) (select rsrc [:a])))))
  

(defn collect-from
  "Collects data from 1 page of search results"
  [url info]
  (map #(do (println "Fetching params from:" %)
            (merge (fetch-params (bn-make-resource %) info) {:url %}))
       (fetch-links (bn-make-resource url))))

(defn collect-data
  "Takes URL of first page of search results
   and collects data from n first pages"
  [url-p1 n info]
  (let [list-pages (cons url-p1 (map #(str url-p1 "&start=" %)
                                     (map #(* % 50) (range 1 n))))]
    (apply concat (map #(collect-from % info) list-pages))))
