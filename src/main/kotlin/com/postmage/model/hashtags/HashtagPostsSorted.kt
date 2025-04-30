package com.postmage.model.hashtags

enum class HashtagPostsSorted(val index: Int) {
    _0(0),
    DESC(1),
    ASC(2),
    POPULAR(3);

    companion object {
        fun getValue(index: Int): HashtagPostsSorted {
            for (i in HashtagPostsSorted.values()) {
                if (i.index == index) return i
            }

            return DESC
        }
    }
}