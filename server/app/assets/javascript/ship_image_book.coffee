$(document).ready ->
  vue = new Vue(vueConf)

vueConf =
  el: '#images'

  data:
    ships: []

  methods:
    getShips: ->
      $.get "/rest/v2/user/#{@userId}/book/ships", (data) =>
        @ships = data

  watch:
    userId: -> @getShips()
