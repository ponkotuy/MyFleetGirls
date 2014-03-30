$(document).ready ->
  $('div.pie').each ->
    json = $(this).attr('data-json')
    raw = JSON.parse(json)
    data = for k, v of raw
      {label: k, data: v}
    option =
      series:
        pie:
          show: true
      grid:
        hoverable: true
    console.log(data)
    console.log(option)
    $.plot($(this), data, option)
