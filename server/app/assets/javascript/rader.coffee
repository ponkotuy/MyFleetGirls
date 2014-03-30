$(document).ready ->
  $('.ship_rader').each () ->
    data = JSON.parse($(this).attr('data-json'))
    radar = [
      color: "green"
      data: data
      spider:
        show: true
        lineWidth: 5
    ]
    option =
      series:
        spider:
          active: true
          legMin: 0
          legMax: 1
          highlight:
            mode: "area"
          legs:
            font: "14px Times New Roman"
            data: [
              {label: "火力"},
              {label: "雷装"},
              {label: "対空"},
              {label: "装甲"},
              {label: "回避"},
              {label: "対潜"},
              {label: "索敵"},
              {label: "運"}
            ]
            legScaleMax: 1
            legScaleMin: 0.6
          spiderSize: 0.7
      grid:
        hoverable: true
        mode: "radar"
    $.plot($(this), radar, option)
