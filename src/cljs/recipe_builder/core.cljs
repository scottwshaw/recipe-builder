(ns recipe-builder.core
  (:require [reagent.core :as r]
            [reagent-forms.core :as rf]
            [reagent.session :as session]
            [secretary.core :as secretary :include-macros true]
            [goog.events :as events]
            [goog.history.EventType :as HistoryEventType]
            [markdown.core :refer [md->html]]
            [recipe-builder.ajax :refer [load-interceptors!]]
            [ajax.core :refer [GET POST]])
  (:import goog.History))

(defn nav-link [uri title page collapsed?]
  [:li.nav-item
   {:class (when (= page (session/get :page)) "active")}
   [:a.nav-link
    {:href uri
     :on-click #(reset! collapsed? true)} title]])

(defn navbar []
  (let [collapsed? (r/atom true)]
    (fn []
      [:nav.navbar.navbar-light.bg-faded
       [:button.navbar-toggler.hidden-sm-up
        {:on-click #(swap! collapsed? not)} "☰"]
       [:div.collapse.navbar-toggleable-xs
        (when-not @collapsed? {:class "in"})
        [:a.navbar-brand {:href "#/"} "recipe-builder"]
        [:ul.nav.navbar-nav
         [nav-link "#/" "Home" :home collapsed?]
         [nav-link "#/grains" "Grains" :grains collapsed?]]]])))

(def orders (r/atom []))

(defn fetch-grains! []
  (GET "http://localhost:8080/grains" {:handler #(session/put! :grains (get % "data"))}))


(defn add-grains [{:keys [quantity grain]}]
  (swap! orders #(conj % {:quantity quantity :grain grain})))

(defn order-panel []
  [:ul.list-group
   (for [{:keys [quantity grain]} @orders]
     [:li.list-group-item {:key (str grain quantity)}
      (str quantity "kg of " grain)])])


(def add-grain-form-template
  [:div.form-group
   [:div.row
    [:div.col-md-2  [:label "type of grain"]]
    [:div.col-md-5
     [:select.form-control {:field :list :id :grain}
      (let [grains (session/get :grains)]
        (println grains)
        (for [g grains]
          [:option {:key g} g]))]]]
   [:div.row
    [:div.col-md-2 [:label "quantity"]]
    [:div.col-md-2 [:input.form-control {:field :numeric :id :quantity}]]]])

(defn grain-form []
  (let [item (atom {})]
    (fn []
      [:div
       [rf/bind-fields add-grain-form-template item]
       [:div.row
        [:button.btn.btn-default {:on-click #(do (println @item) (add-grains @item))} "add grain to order"]]])))
       

(defn grains-page []
  [:div.container
   [:p]
   [:div.row
    [:h2 "Current Order"]]
   [:div.row [order-panel]]
   [:p]
   [:div.row [:h2 "Add a grain"]]
   [:div.row
    [:div.col-md-12
     [grain-form]]]])

(defn home-page []
  [:div.container
   [:div.jumbotron
    [:h1 "Welcome to recipe-builder"]
    [:p "Time to start building your site!"]
    [:p [:a.btn.btn-primary.btn-lg {:href "http://luminusweb.net"} "Learn more »"]]]
   [:div.row
    [:div.col-md-12
     [:h2 "Welcome to ClojureScript"]]]
   (when-let [docs (session/get :docs)]
     [:div.row
      [:div.col-md-12
       [:div {:dangerouslySetInnerHTML
              {:__html (md->html docs)}}]]])])

(def pages
  {:home #'home-page
   :grains #'grains-page})

(defn page []
  [(pages (session/get :page))])

;; -------------------------
;; Routes
(secretary/set-config! :prefix "#")

(secretary/defroute "/" []
  (session/put! :page :home))

(secretary/defroute "/grains" []
  (session/put! :page :grains))

;; -------------------------
;; History
;; must be called after routes have been defined
(defn hook-browser-navigation! []
  (doto (History.)
        (events/listen
          HistoryEventType/NAVIGATE
          (fn [event]
              (secretary/dispatch! (.-token event))))
        (.setEnabled true)))

;; -------------------------
;; Initialize app
(defn fetch-docs! []
  (GET (str js/context "/docs") {:handler #(session/put! :docs %)}))

(defn mount-components []
  (r/render [#'navbar] (.getElementById js/document "navbar"))
  (r/render [#'page] (.getElementById js/document "app")))

(defn init! []
  (load-interceptors!)
  (fetch-grains!)
  (fetch-docs!)
  (hook-browser-navigation!)
  (mount-components))
