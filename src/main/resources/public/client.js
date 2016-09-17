var apiUrl = "";

function Searcher() {

	this.apiUrl = apiUrl;
	this.suggestUrl = this.apiUrl + "/suggest"

	this.searchForm = $("#search-form");
	this.searchInput = $("#search-term");

	this.bindSuggest = function() {
		var _this = this;
		if (this.searchInput.length > 0) {
			this.searchInput.autocomplete({
				minLength: 2,
				select: function(event, element) {
					_this.searchInput.val(element.item.value);
					_this.searchForm.submit();
				},
				source: this.suggestUrl
			}).data("ui-autocomplete")._renderItem = function(ul, item) {
				var li =  $("<li>");
				li.text(item.value);
				if (!item.prefix) {
					li.addClass("suggestion");
				}
				return li.appendTo(ul);
			}
		}
	};

	this.focusInput = function() {
		if (this.searchInput != null) {
			this.searchInput.select();
			this.searchInput.focus();
		}
	};

}

function Switcher() {

	this.switchEntryToInline = function(entry) {
		entry.removeClass("view-tree");
		entry.addClass("view-inline");
	};

	this.switchEntryToTree = function(entry) {
		entry.removeClass("view-inline");
		entry.addClass("view-tree");
	};

	this.bindSwitches = function() {
		var _this = this;
		$(".switch-view-inline").click(function(event) {
			$(this).css("font-weight", "bold");
			var pSwitch = $(this).closest("div.switch-container");
			var treeLink = pSwitch.find(".switch-view-tree");
			treeLink.css("font-weight", "normal");
			var entry = $(this).closest(".entry");
			_this.switchEntryToInline(entry);
			event.preventDefault();
		});
		$(".switch-view-tree").click(function(event) {
			$(this).css("font-weight", "bold");
			var pSwitch = $(this).closest("div.switch-container");
			var inlineLink = pSwitch.find(".switch-view-inline");
			inlineLink.css("font-weight", "normal");
			var entry = $(this).closest(".entry");
			_this.switchEntryToTree(entry);
			event.preventDefault();
		});
	};

}

$(document).ready(function() {

	var searcher = new Searcher();
	searcher.bindSuggest();
	searcher.focusInput();

	var switcher = new Switcher();
	switcher.bindSwitches();

});
