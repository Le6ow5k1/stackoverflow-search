(ns stackoverflow-search.test.handler
  (:use clojure.test
        stackoverflow-search.handler))

(deftest aggregate-tags-test
  (testing "when there is answered and unanswered questions"
    (let [questions [{:is_answered true
                    :tags ["clojure" "lisp"]}
                   {:is_answered false
                    :tags ["java"]}
                   ]
          result (aggregate-tags questions)]
      (is (= result {"clojure" {:total 1 :answered 1}
                     "lisp" {:total 1 :answered 1}
                     "java" {:total 1 :answered 0}})))
    )

  (testing "when tags repeat among questions"
    (let [questions [{:is_answered true
                    :tags ["clojure" "lisp"]}
                   {:is_answered false
                    :tags ["java" "clojure"]}
                   {:is_answered true
                    :tags ["lisp"]}
                   ]
          result (aggregate-tags questions)]
      (is (= result {"clojure" {:total 2 :answered 1}
                     "lisp" {:total 2 :answered 2}
                     "java" {:total 1 :answered 0}})))
    )
  )
