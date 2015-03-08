$(document).ready ->
  fromShip = new Vue
    el: '#from_name'

    data:
      sCounts: []
      dropCounts: []
      isOver: false
      MaxDropSize: 50

    methods:
      getSCounts: (sid) ->
        $.getJSON "/rest/v1/recipe/from_ship/#{sid}", {}, (ret) =>
          @sCounts = ret
        $.getJSON "/rest/v1/drop_from_ship/#{sid}", {}, (ret) =>
          @dropCounts = ret.slice(0, @MaxDropSize)
          @isOver = ret.length > @MaxDropSize

    created: ->
      sid = $(@.$options.el).attr('data-ship-id')
      @getSCounts(sid)

  image = new Vue
    el: '#ship_image'
    data:
      damaged: false
    methods:
      change: -> @damaged = !@damaged
