
# Require graph.coffee

@tweet = (name, lv) ->
  here = encodeURIComponent(location.href)
  text = "#{name} Lv#{lv}"
  url = "https://twitter.com/intent/tweet?original_referer=#{here}&url=#{here}&text=#{text}&hashtags=MyFleetGirls"
  window.open(url)

# data required {userId: `userId`, shipId: `shipId`}
@yome = (userId, shipId) ->
  $.post('/passwd/post/v1/settings', {userId: userId, shipId: shipId})
    .success -> location.href = "/user/#{userId}/top"
    .error (e) -> console.error(e)
