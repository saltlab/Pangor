onLoaded: function () {
			var self = this;
			this.addloadmore();

			this.AddGhostsToBottomRow();
			$(window).resize(function () {
				var addghost;
				clearTimeout(addghost);
				addghost = setTimeout(function () {
					self.AddGhostsToBottomRow();
				}, 100);
			});

			if (typeof (this.ui.spinner) === 'object') {
				this.ui.spinner.hide();
			}

			$('.filter-bar').on('mousedown', function (e) {
				if (e.target.localName !== 'div') {
					return;
				}
				_.defer(function () {
					self.$('.items:first').focus();
				});
			});
			$('.items').attr('tabindex', '1');
			_.defer(function () {
				self.checkEmpty();
				self.$('.items:first').focus();
			});

		}