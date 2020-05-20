import SwiftUI

extension Color {
  // MARK: Primary - #03A9F4
  static let primaryColor: Color = Color(
    red: 43/255,
    green: 172/255,
    blue: 252/255
  )
  
  // MARK: Background - #E5E5E5
  static let backgroundColor: Color = Color(
    red: 229/255,
    green: 229/255,
    blue: 229/255
  )
  
  // MARK: Color -> UIColor
  func uiColor() -> UIColor {
    let components = self.components()
    return UIColor(
      red: components.red,
      green: components.green,
      blue: components.blue,
      alpha: components.alpha
    )
  }

  private func components() -> (
    red: CGFloat, green: CGFloat, blue: CGFloat, alpha: CGFloat
  ) {
    let scanner = Scanner(string: self.description.trimmingCharacters(in: CharacterSet.alphanumerics.inverted))
    
    var hexNumber: UInt64 = 0
    var r: CGFloat = 0.0
    var g: CGFloat = 0.0
    var b: CGFloat = 0.0
    var a: CGFloat = 0.0

    let result = scanner.scanHexInt64(&hexNumber)
    if result {
      r = CGFloat((hexNumber & 0xff000000) >> 24) / 255
      g = CGFloat((hexNumber & 0x00ff0000) >> 16) / 255
      b = CGFloat((hexNumber & 0x0000ff00) >> 8) / 255
      a = CGFloat(hexNumber & 0x000000ff) / 255
    }
    
    return (r, g, b, a)
  }
}
