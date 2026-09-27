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
		var input = this.searchInput.get(0),
			form = this.searchForm.get(0),
			list = document.getElementById("search-suggestions"),
			controller = null,
			requestId = 0,
			results = [],
			activeIndex = -1,
			_this = this;
		if (input == null || form == null || list == null) {
			return;
		}
		function closeList() {
			list.hidden = true;
			list.replaceChildren();
			results = [];
			input.setAttribute("aria-expanded", "false");
			input.removeAttribute("aria-activedescendant");
			activeIndex = -1;
		}
		function cancelRequest() {
			requestId++;
			if (controller !== null) {
				controller.abort();
				controller = null;
			}
		}
		function activate(index) {
			var options = list.querySelectorAll('[role="option"]');
			activeIndex = index;
			options.forEach(function(option, optionIndex) {
				var active = optionIndex === activeIndex;
				option.setAttribute("aria-selected", active ? "true" : "false");
				option.classList.toggle("active", active);
			});
			if (activeIndex >= 0) {
				input.setAttribute("aria-activedescendant", options[activeIndex].id);
				options[activeIndex].scrollIntoView({block: "nearest"});
			}
		}
		function select(result) {
			input.value = result.value;
			closeList();
			form.requestSubmit();
		}
		input.addEventListener("input", function() {
			var term = input.value,
				currentRequest = ++requestId;
			if (controller !== null) {
				controller.abort();
				controller = null;
			}
			closeList();
			results = [];
			if (term.length < 2) {
				return;
			}
			controller = new AbortController();
			fetch(_this.suggestUrl + "?term=" + encodeURIComponent(term), {signal: controller.signal})
				.then(function(response) {
					if (!response.ok) {
						throw new Error("Suggestion request failed");
					}
					return response.json();
				})
				.then(function(items) {
					if (currentRequest !== requestId || input.value !== term || !Array.isArray(items)) {
						return;
					}
					controller = null;
					results = items;
					items.forEach(function(result, index) {
						var option = document.createElement("div");
						option.id = "search-suggestion-" + index;
						option.className = "search-suggestion";
						option.setAttribute("role", "option");
						option.setAttribute("aria-selected", "false");
						option.textContent = result.value;
						if (!result.prefix) {
						option.classList.add("suggestion");
						}
						option.addEventListener("mousedown", function(event) {
							event.preventDefault();
						});
						option.addEventListener("click", function() {
							select(result);
						});
						option.addEventListener("mouseenter", function() {
							activate(index);
						});
						list.append(option);
					});
					if (items.length > 0) {
						list.hidden = false;
						input.setAttribute("aria-expanded", "true");
					}
				}, function(error) {
					if (error.name !== "AbortError") {
						console.error("Suggestion request failed", error);
					}
				});
		});
		input.addEventListener("keydown", function(event) {
			if (results.length === 0) {
				return;
			}
			if (event.key === "ArrowDown" || event.key === "ArrowUp") {
				event.preventDefault();
				var direction = event.key === "ArrowDown" ? 1 : -1,
					next = activeIndex + direction;
				if (next < 0) {
					next = results.length - 1;
				} else if (next >= results.length) {
					next = 0;
				}
				activate(next);
			} else if (event.key === "Enter" && activeIndex >= 0) {
				event.preventDefault();
				select(results[activeIndex]);
			} else if (event.key === "Escape") {
				event.preventDefault();
				cancelRequest();
				closeList();
			}
		});
		input.addEventListener("blur", function() {
			cancelRequest();
			closeList();
		});
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
		var date = new Date(),
			expires = "";
		// The cookie expires in 30 days
		date.setTime(date.getTime() + 2592000000);
		expires = "expires="+ date.toUTCString();
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
			$(this).removeClass("inactive");
			var pSwitch = $(this).closest("div.switch-container"),
				treeLink = pSwitch.find(".switch-view-tree"),
				entry = $(this).closest(".entry");
			treeLink.addClass("inactive");
			_this.switchEntryToInline(entry);
			event.preventDefault();
		});
		$(".switch-view-tree").click(function(event) {
			$(this).removeClass("inactive");
			var pSwitch = $(this).closest("div.switch-container"),
				inlineLink = pSwitch.find(".switch-view-inline"),
				entry = $(this).closest(".entry");
			inlineLink.addClass("inactive");
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
