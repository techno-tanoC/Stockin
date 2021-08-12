package dev.tanoc.android.stockin.repository

import dev.tanoc.android.stockin.model.Item

interface IItemRepository {
    fun index(): List<Item>
}

class ItemRepository : IItemRepository {
    override fun index(): List<Item> {
        return listOf()
    }
}

class MockItemRepository : IItemRepository {
    private val items = mutableListOf(
        Item(
            "YouTube",
            "https://www.youtube.com/"
        ),
        Item(
            "ニコニコ動画",
            "https://www.nicovideo.jp/"
        ),
        Item(
            "Google Drive",
            "https://drive.google.com/"
        ),
        Item(
            "GMail",
            "https://mail.google.com/"
        ),
        Item(
            "Twitter",
            "https://twitter.com/home"
        ),
        Item(
            "GitHub",
            "https://github.com/"
        ),
        Item(
            "GitLab",
            "https://gitlab.com/"
        ),
        Item(
            "Zenn",
            "https://zenn.dev/"
        ),
        Item(
            "Qiita",
            "https://qiita.com/"
        ),
        Item(
            "Rustプログラミング言語",
            "https://www.rust-lang.org/ja"
        ),
        Item(
            "Haskell Language",
            "https://www.haskell.org/"
        ),
        Item(
            "Docker",
            "https://www.docker.com/"
        ),
        Item(
            "The Go Programming Language",
            "https://golang.org/"
        ),
        Item(
            "TypeScript",
            "https://www.typescriptlang.org/"
        ),
        Item(
            "Android",
            "https://www.android.com/"
        ),
    )

    override fun index(): List<Item> {
        return items.toList()
    }

    fun prepend(item: Item) {
        items.add(0, item)
    }
}
