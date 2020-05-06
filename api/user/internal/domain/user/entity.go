package domain

import "time"

// User - Userエンティティ
type User struct {
	ID           string    `firestore:"id"`
	Name         string    `firestore:"name"`
	DisplayName  string    `firestore:"display_name"`
	Email        string    `firestore:"email"`
	ThumbnailURL string    `firestore:"thumbnail_url"`
	Password     string    `firestore:"-"`
	CreatedAt    time.Time `firestore:"created_at"`
	UpdatedAt    time.Time `firestore:"updated_at"`
}
