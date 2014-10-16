(ns com.dydra.kr.sesame.rdf
  (import
   java.io.IOException

   ;interfaces
   org.openrdf.model.URI
   org.openrdf.model.Resource
   org.openrdf.model.Statement
   org.openrdf.model.Literal

   ;Classes
   org.openrdf.model.impl.StatementImpl
   org.openrdf.model.impl.URIImpl
   org.openrdf.model.impl.LiteralImpl

   org.openrdf.repository.Repository
   org.openrdf.repository.http.HTTPRepository
   org.openrdf.repository.RepositoryConnection
   org.openrdf.query.resultio.TupleQueryResultFormat
   org.openrdf.rio.RDFFormat))

;;; --------------------------------------------------------
;;; specials
;;; --------------------------------------------------------

;;; --------------------------------------------------------
;;; helpers
;;; --------------------------------------------------------

;;; --------------------------------------------------------
;;; sesame specific connection
;;; --------------------------------------------------------

;;; --------------------------------------------------------
;;; graphs and models
;;; --------------------------------------------------------

;;; --------------------------------------------------------
;;; namespaces
;;; --------------------------------------------------------

;;; --------------------------------------------------------
;;; sesame-ify
;;; --------------------------------------------------------

(defn sesame-create-language-string-literal
  ([kb s lang]
     (.createLiteral (:value-factory kb)
                     s
                     lang)))

(defn sesame-create-string-literal
  ([kb s]
     (.createLiteral (:value-factory kb)
                     s)))


;;; --------------------------------------------------------
;;; clj-ify
;;; --------------------------------------------------------

;;; literal-types
;;; --------------------------------------------------------

;;; clj-ify
;;; --------------------------------------------------------

;;; --------------------------------------------------------
;;; adding
;;; --------------------------------------------------------

;;; --------------------------------------------------------
;;; querying
;;; --------------------------------------------------------

;;; --------------------------------------------------------
;;; END
;;; --------------------------------------------------------
