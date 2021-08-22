package dev.tanoc.android.stockin.repository

import dev.tanoc.android.stockin.model.Item

class MockItemRepository : IItemRepository {
    private val items = mutableListOf(
        Item(
            1,
            "YouTube",
            "https://www.youtube.com/"
        ),
        Item(
            2,
            "ニコニコ動画",
            "https://www.nicovideo.jp/"
        ),
        Item(
            3,
            "Google Drive",
            "https://drive.google.com/"
        ),
        Item(
            4,
            "GMail",
            "https://mail.google.com/"
        ),
        Item(
            5,
            "Twitter",
            "https://twitter.com/home"
        ),
        Item(
            6,
            "GitHub",
            "https://github.com/"
        ),
        Item(
            7,
            "GitLab",
            "https://gitlab.com/"
        ),
        Item(
            8,
            "Zenn",
            "https://zenn.dev/"
        ),
        Item(
            9,
            "Qiita",
            "https://qiita.com/"
        ),
        Item(
            10,
            "Rustプログラミング言語",
            "https://www.rust-lang.org/ja"
        ),
        Item(
            11,
            "Haskell Language",
            "https://www.haskell.org/"
        ),
        Item(
            12,
            "Docker",
            "https://www.docker.com/"
        ),
        Item(
            13,
            "The Go Programming Language",
            "https://golang.org/"
        ),
        Item(
            14,
            "TypeScript",
            "https://www.typescriptlang.org/"
        ),
        Item(
            15,
            "Android",
            "https://www.android.com/"
        ),
    )

    override suspend fun index(): List<Item> {
        return items.toList()
    }

    override suspend fun create(title: String, url: String) {
        items.add(Item(items.size + 1, title, url))
    }
}
