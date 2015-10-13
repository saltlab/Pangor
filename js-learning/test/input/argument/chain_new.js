metadata = xmlb.create('DIDL-Lite', {
				'headless': true
			})
			.att({
				'xmlns': 'urn:schemas-upnp-org:metadata-1-0/DIDL-Lite/',
				'xmlns:dc': 'http://purl.org/dc/elements/1.1/',
				'xmlns:upnp': 'urn:schemas-upnp-org:metadata-1-0/upnp/',
				'xmlns:dlna': 'urn:schemas-dlna-org:metadata-1-0/',
				'xmlns:sec': 'http://www.sec.co.kr/',
				'xmlns:xbmc': 'urn:schemas-xbmc-org:metadata-1-0/'
			})
			.ele('item', {
				'id': '0',
				'parentID': '-1',
				'restricted': '1'
			})
			.ele('dc:title', {}, 'Popcorn Time Video')
			.insertAfter('res', {
				'protocolInfo': 'http-get:*:video/mp4:*',
				'xmlns:pv': 'http://www.pv.com/pvns/',
				'pv:subtitleFileUri': url_subtitle,
				'pv:subtitleFileType': 'srt'
			}, url_video)
			.insertAfter('res', {
				'protocolInfo': 'http-get:*:text/srt:'
			}, url_subtitle)
			.insertAfter('res', {
				'protocolInfo': 'http-get:*:smi/caption'
			}, url_subtitle)
			.insertAfter('sec:CaptionInfoEx', {
				'sec:type': 'srt'
			}, url_subtitle)
			.insertAfter('sec:CaptionInfo', {
				'sec:type': 'srt'
			}, url_subtitle)
			.insertAfter('upnp:class', {}, 'object.item.videoItem.movie')
			.end({
				pretty: false
			});