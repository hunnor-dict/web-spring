var apiUrl = "";

function Searcher() {

	this.apiUrl = apiUrl;
	this.suggestUrl = this.apiUrl + "/suggest"

	this.searchForm = $("#search-form");
	this.searchDelete = $("#search-delete");
	this.searchInput = $("#search-term");

	this.setCursorPosition = function(pos) {
		if (this.searchInput.get(0).setSelectionRange) {
			this.searchInput.get(0).setSelectionRange(pos, pos);
		} else if (this.searchInput.get(0).createTextRange) {
			var range = this.searchInput.get(0).createTextRange();
			range.collapse(true);
			range.moveEnd('character', pos);
			range.moveStart('character', pos);
			range.select();
		}
	}

	this.bindChars = function() {
		var _this = this;
		this.searchForm.find("button.letter").click(function() {
			var ch = $(this).data("char"),
				selectionStart = document.getElementById("search-term").selectionStart,
				selectionEnd = document.getElementById("search-term").selectionEnd,
				before = _this.searchInput.val();
				after = before.substring(0, selectionStart)
					+ ch
					+ before.substring(selectionEnd, before.length);
			_this.searchInput.val(after);
			_this.focusInput();
			_this.setCursorPosition(selectionStart + 1);
		});
	};

	this.bindDelete = function() {
		var _this = this;
		this.searchDelete.click(function() {
			_this.searchInput.val("");
			_this.focusInput();
		});
	}

	this.bindSuggest = function() {
		var _this = this;
		if (this.searchInput.length > 0) {
			this.searchInput.autocomplete({
				minLength: 2,
				select: function(event, element) {
					_this.searchInput.val(element.item.value);
					_this.searchForm.submit();
				},
				open: function(event, element) {
					$(".ui-autocomplete").off("menufocus");
				},
				source: this.suggestUrl
			}).data("ui-autocomplete")._renderItem = function(ul, item) {
				var div = $("<div>"),
					li =  $("<li>");
				div.addClass("ui-menu-item-wrapper");
				div.text(item.value);
				li.addClass("ui-menu-item");
				if (!item.prefix) {
					li.addClass("suggestion");
				}
				div.appendTo(li);
				return li.appendTo(ul);
			}
		}
	};

	this.focusInput = function() {
		if (this.searchInput != null) {
			this.searchInput.focus();
		}
	};

}

function Settings() {

	this.bindSettings = function() {
		var _this = this;
		$("#settings-toggle").click(function(event) {
			$("#settings-panel").toggle();
			event.preventDefault();
		});
		$("#view-inline").click(function(event) {
			_this.setCookie("view", "inline");
			$(this).css("font-weight", "bold");
			$("#view-tree").css("font-weight", "normal");
			event.preventDefault();
		});
		$("#view-tree").click(function(event) {
			_this.setCookie("view", "tree");
			$(this).css("font-weight", "bold");
			$("#view-inline").css("font-weight", "normal");
			event.preventDefault();
		});
	}

	this.setCookie = function(cookieName, cookieValue) {
		var date = new Date();
		// The cookie expires in 30 days
		date.setTime(date.getTime() + 2592000000);
		var expires = "expires="+ date.toUTCString();
		document.cookie = cookieName + "=" + cookieValue + ";" + expires;
	}

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
			var pSwitch = $(this).closest("div.switch-container"),
				treeLink = pSwitch.find(".switch-view-tree"),
				entry = $(this).closest(".entry");
			treeLink.css("font-weight", "normal");
			_this.switchEntryToInline(entry);
			event.preventDefault();
		});
		$(".switch-view-tree").click(function(event) {
			$(this).css("font-weight", "bold");
			var pSwitch = $(this).closest("div.switch-container"),
				inlineLink = pSwitch.find(".switch-view-inline"),
				entry = $(this).closest(".entry");
			inlineLink.css("font-weight", "normal");
			_this.switchEntryToTree(entry);
			event.preventDefault();
		});
	};

}

$(document).ready(function() {

	var searcher = new Searcher(),
		settings = new Settings(),
		switcher = new Switcher();

	searcher.bindChars();
	searcher.bindDelete();
	searcher.bindSuggest();
	searcher.focusInput();

	settings.bindSettings();

	switcher.bindSwitches();

});
