fun List<Int>.isSorted() = windowed(2).all { (_0, _1) -> _0 <= _1 }
