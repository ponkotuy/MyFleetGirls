$(document).ready ->
  vue = new Vue(vueConf)

vueConf =
  data:
    ships = []

  methods:
    getShips: ->
      $.get "/rest/v1/"
