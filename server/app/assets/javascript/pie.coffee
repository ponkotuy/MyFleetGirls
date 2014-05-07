$(document).ready ->
  $('div.pie').each ->
    json = $(this).attr('data-json')
    data = JSON.parse(json)
    option =
      series:
        pie:
          show: true
          radius: 1
          label:
            show: true
            radius: 3/4
            formatter: (label, series) ->
              """<div style="font-size:8pt;text-align:center;padding:2px;color:white;">#{label}<br/>#{Math.round(series.percent*10)/10.0}%</div>"""
            background:
              opacity: 0.5
              color: '#000'
      legend:
        show: false
      grid:
        hoverable: true
        clickable: true

    $.plot($(this), data, option)
    $(this).bind "plotclick", (event, pos, item) ->
      location.href = "/entire/sta/from_ship#query=#{item.series.label}"
