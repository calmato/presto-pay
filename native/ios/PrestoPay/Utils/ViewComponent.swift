import Foundation
import SwiftUI

extension View {
  func underline() -> some View {
    return VStack {
      self
      Divider()
    }
  }
}
