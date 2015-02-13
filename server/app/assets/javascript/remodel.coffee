$(document).ready ->
  $('.panel').each ->
    elem = $(this)
    id = elem.attr('id')
    slot = elem.attr('data-slot')
    vue = new Vue
      el: '#' + id
      data:
        url: ""
        dayOfWeek: []
        secondShip: []
      methods:
        getJSON: ->
          @setHash()
          $.getJSON @url, (data) =>
            @dayOfWeek = data.dayOfWeek
            @secondShip = data.secondShip.slice(0, 5)
        setHash: ->
          location.href = '#' + toURLParameter({id: slot})
        viewDayOfWeek: (day) -> '月火水木金土日'[day - 1]
        restoreHash: ->
          fromURLParameter(location.hash.replace(/^\#/, ''))
      created: ->
        i = this
        elem.find('.panel-collapse').on 'show.bs.collapse', ->
          i.restoreHash()
          i.url = $(this).attr('data-url')
          if i.dayOfWeek.length == 0
            i.getJSON()
      watch:
        dayOfWeek: () ->
          if @dayOfWeek.length > 0
            $("#panel#{slot}")[0].scrollIntoView(true)

  obj = fromURLParameter(location.hash.replace(/^\#/, ''))
  $("#collapse#{obj.id}").collapse()
